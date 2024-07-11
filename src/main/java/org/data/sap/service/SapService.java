package org.data.sap.service;

public interface SapService<T> {
//	T requestPost(String url,  json, T response);
	T requestGet(String url, T response);
}
