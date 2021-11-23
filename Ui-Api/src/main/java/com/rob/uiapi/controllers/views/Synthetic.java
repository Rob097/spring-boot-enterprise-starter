package com.rob.uiapi.controllers.views;

import com.rob.uiapi.controllers.views.IView.View;

@View(name = Synthetic.name)
public class Synthetic implements IView {
	public static final String name = "synthetic";
	public static final Synthetic value = new Synthetic();
	
	public Synthetic() {};
	
	public Synthetic(String string){
		
	}
}
