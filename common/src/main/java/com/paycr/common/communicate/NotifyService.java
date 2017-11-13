package com.paycr.common.communicate;

import org.springframework.stereotype.Service;

@Service
public interface NotifyService<T> {

	public void notify(T t);

	public String getEmail(T t) throws Exception;

	public String getSms(T t) throws Exception;

}
