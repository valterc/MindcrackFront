package com.vcutils.infinitelist;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.vcutils.WLog;

public abstract class InfiniteListAdapter<T> extends BaseAdapter implements OnScrollListener {

	private ListView listView;
	private Handler handler;
	private OnScrollListener onScrollListener;
	protected IInfiniteListDataProvider<T> dataProvider;
	private LayoutInflater inflater;
	protected LinkedList<T> items;
	private Integer startItemIndex;
	private Integer endItemIndex;
	private Integer visibleItemCount;
	
	private boolean isLoadingData;
	private boolean endOfItems;
	private boolean showError;

	public InfiniteListAdapter(IInfiniteListDataProvider<T> dataProvider, Context context, ListView listView) {
		this.listView = listView;
		this.handler = new Handler();
		this.dataProvider = dataProvider;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = new LinkedList<T>();
		this.endOfItems = false;
		this.startItemIndex = 0;
		this.endItemIndex = 0;

		listView.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		return items.size() + (endOfItems ? items.size() == 0 ? 1 : 0 : 1);
	}

	@Override
	public T getItem(int position) {
		if (position < 0 || position > items.size() - 1)
			return null;
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (position == this.items.size() && !endOfItems && !showError) {
			if (!isLoadingData) {
				getMoreItems();
			}
			return inflateLoadingLayout(this.inflater);
		} else if (items.size() == 0 && endOfItems) {
			return inflateEmptyLayout(this.inflater);
		} else if (position == 0 && items.size() == 0 && showError) {
				return inflateInitErrorLayout(this.inflater);
		} else if (position == this.items.size() && showError) {
			return inflateErrorLayout(this.inflater);
		}

		if (convertView == null || convertView.getTag() == null)
			vi = inflateNewLayout(this.inflater, position);

		vi = populateLayout(vi, position);
		return vi;
	}

	public void setEndOfItems(boolean b) {
		this.endOfItems = b;
		this.isLoadingData = false;
		handler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void setShowError(boolean b) {
		this.showError = b;
		this.isLoadingData = false;
		handler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	protected void getMoreItems() {
		this.isLoadingData = true;
		if (dataProvider != null)
			dataProvider.dataReachedEnd();
	}

	public int getItemIndex(T item){
		return items.indexOf(item);
	}
	
	public int getStartIndex(){
		return startItemIndex;
	}
	
	public int getEndIndex(){
		return endItemIndex;
	}
	
	public Boolean isItemVisible(T item) {
		Boolean result = true;
		synchronized (this.startItemIndex) {
			int index = items.indexOf(item);
			if (index == -1)
				result = false;
			else if (index < startItemIndex || index > endItemIndex)
				result = false;
		}
		return result;
	}

	public void AddItems(T... items) {
		for (int i = 0; i < items.length; i++) {
			this.items.add(items[i]);
		}
		this.isLoadingData = false;
		updateItems();
	}
	
	public void AddItems(ArrayList<T> items) {
		for (int i = 0; i < items.size(); i++) {
			this.items.add(items.get(i));
		}
		this.isLoadingData = false;
		updateItems();
	}

	public void clearItems(){
		this.items.clear();
		this.endOfItems = true;
		updateItems();
	}
	
	public void updateItems(){
		this.handler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}
	
	public View getVisibleViewOfPosition(int position){
		
		int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); 
		int wantedChild = position - firstPosition;
		
		if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
		  WLog.logDebug(InfiniteListAdapter.class.getName(), "Unable to get view for desired position, because it's not being displayed on screen.");
		  return null;
		}
		
		return listView.getChildAt(wantedChild);
	}
	
	public int getVisibleItemCount(){
		return this.visibleItemCount;
	}
	
	public abstract View inflateLoadingLayout(LayoutInflater inflater);

	public abstract View inflateEmptyLayout(LayoutInflater inflater);

	public abstract View inflateNewLayout(LayoutInflater inflater, int position);

	public abstract View populateLayout(View v, int position);

	public abstract View inflateErrorLayout(LayoutInflater inflater);
	
	public abstract View inflateInitErrorLayout(LayoutInflater inflater);

	public OnScrollListener getOnScrollListener() {
		return onScrollListener;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		synchronized (this.startItemIndex) {
			this.startItemIndex = firstVisibleItem;
			this.endItemIndex = firstVisibleItem + visibleItemCount - 1;
			this.visibleItemCount = visibleItemCount;
		}
		
		// WLog.logDebug("InfiniteListAdapter", "Start index: " + startItemIndex
		// + "  --  end index: " + endItemIndex + " -- visible: " +
		// visibleItemCount);

		if (onScrollListener != null)
			onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (onScrollListener != null)
			onScrollListener.onScrollStateChanged(view, scrollState);
	}

	public IInfiniteListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(IInfiniteListDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

}
