package com.rob.uiapi.controllers.views;

import com.rob.uiapi.controllers.views.IView.View;

@View(name = Verbose.name)
public class Verbose implements IView {
	public static final String name = "verbose";
	public static final Verbose value = new Verbose();
}
