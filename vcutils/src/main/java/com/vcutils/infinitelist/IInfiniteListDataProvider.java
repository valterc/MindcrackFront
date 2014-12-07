package com.vcutils.infinitelist;

import java.util.ArrayList;

public interface IInfiniteListDataProvider<T> {

	public void dataReachedEnd();
	public void disposeItems(ArrayList<T> posts);
	
}
