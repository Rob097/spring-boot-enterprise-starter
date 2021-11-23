package com.rob.uiapi.controllers.views;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;

public interface IView {
	@Target({ ElementType.TYPE, ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface View {
		/**
		 * Alias for {@link #name}.
		 */
		@AliasFor("name")
		String value() default "";

		/**
		 * The name of the request parameter to bind to.
		 */
		@AliasFor("value")
		String name() default "";
	}

	/**
	 * Equivale a {@link #isExactly(Class)} passando view.getClass()
	 * 
	 * @param view
	 * @return
	 */
	default boolean isExactly(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return isExactly(view.getClass());
	}

	/**
	 * Verifica se questa vista coincide esattamente con quella passata in input
	 * 
	 * @param view
	 * @return
	 */
	default boolean isExactly(Class<? extends IView> view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return view.equals(this.getClass());
	}

	/**
	 * Equivale a {@link #isAtLeast(Class)} passando view.getClass()
	 * 
	 * @param view
	 * @return
	 */
	default boolean isAtLeast(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return isAtLeast(view.getClass());
	}

	/**
	 * Verifica se questa vista include (ovvero è una sottoclasse) della vista
	 * passata in input
	 * 
	 * @param view
	 * @return
	 */
	default boolean isAtLeast(Class<? extends IView> view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return view.isInstance(this);
	}

	/**
	 * Equivale a {@link #isAtMost(Class)} passando view.getClass()
	 * 
	 * @param view
	 * @return
	 */
	default boolean isAtMost(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return isAtMost(view.getClass());
	}

	/**
	 * Verifica se questa vista è uguale o inferiore (ovvero una superclasse) della
	 * vista passata in input
	 * 
	 * @param view
	 * @return
	 */
	default boolean isAtMost(Class<? extends IView> view) {
		if (view == null) {
			throw new IllegalArgumentException("View argument cannot be null");
		}

		return this.getClass().isAssignableFrom(view);
	}
}
