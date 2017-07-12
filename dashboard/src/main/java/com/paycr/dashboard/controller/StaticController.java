package com.paycr.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;

@RestController
public class StaticController {

	@RequestMapping("/html/{folder}/{file}")
	public ModelAndView getTemplate(@PathVariable String folder, @PathVariable String file) {
		return new ModelAndView("html/" + folder + "/" + file);
	}

	@RequestMapping("/enum/providers")
	public List<String> getParamProviders() {
		List<String> providers = new ArrayList<String>();
		for (ParamValueProvider provider : ParamValueProvider.values()) {
			providers.add(provider.name());
		}
		return providers;
	}

	@RequestMapping("/enum/paymodes")
	public List<String> getPayModes() {
		List<String> payModes = new ArrayList<String>();
		for (PayMode payMode : PayMode.values()) {
			payModes.add(payMode.name());
		}
		return payModes;
	}

}
