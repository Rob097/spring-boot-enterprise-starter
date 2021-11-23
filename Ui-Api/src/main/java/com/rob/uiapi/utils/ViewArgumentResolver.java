package com.rob.uiapi.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.MethodParameter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.rob.uiapi.controllers.views.IView;


/**
 * @author Roberto97
 * The Bean of this object is created in UIApiWebConfiguration
 * Is used to manage the view object passed as parameter to restServices.
 */
public class ViewArgumentResolver implements HandlerMethodArgumentResolver {
	public static final String RESOLVED_VIEW_REQUEST_ATTRIBUTE = "_resolved_view";
	public static final String VIEW_PACKAGE_NAME = "views";
	private static final String rootPackage = IView.class.getPackage().getName();
	private final RequestParamMethodArgumentResolver wrappedResolver;
	private final Map<String, Object> cache = new ConcurrentHashMap<>();

	private final Logger log = LoggerFactory.getLogger(getClass());

	public ViewArgumentResolver() {
		wrappedResolver = new RequestParamMethodArgumentResolver(true);
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		if (!methodParameter.getParameterType().equals(IView.class)){
			return false;
		}
		if (methodParameter.hasParameterAnnotation(RequestParam.class)){
			return wrappedResolver.supportsParameter(methodParameter);
		}
		if (methodParameter.hasParameterAnnotation(RequestPart.class)){
			return false;
		}
		return true;

	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception{

		Object paramValue = wrappedResolver.resolveArgument(parameter, mavContainer, webRequest, null);
		if (paramValue == null) {
			return null;
		}
		if (!(paramValue instanceof String)) {
			throw new ServletRequestBindingException("Cannot bind parameter value " + paramValue + " to " + IView.class + " class");
		}

		String requestedViewName = (String) paramValue;

		String packageName = parameter.getDeclaringClass().getPackage().getName();

		String cacheKey = requestedViewName + "@" + packageName;

		Object resolvedView = null;

		if (cache.containsKey(cacheKey)) {
			resolvedView = cache.get(cacheKey);
			handleResolvedValue(resolvedView, parameter, mavContainer, webRequest);
			return resolvedView;
		}

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(IView.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(IView.View.class));

		do {
			Set<BeanDefinition> scanned = scanner.findCandidateComponents(packageName + "." + VIEW_PACKAGE_NAME);

			for (BeanDefinition b : scanned) {
				try {
					Class<?> clz = Class.forName(b.getBeanClassName());
					IView.View v = clz.getAnnotation(IView.View.class);
					String viewName = clz.getSimpleName().toLowerCase();
					if (StringUtils.isNotBlank(v.name())) {
						viewName = v.name();
					}

					if (viewName.equals(requestedViewName)) {
						resolvedView = clz.newInstance();
						break;
					}
				} catch (Exception e) {
					log.warn("Something unexpected occurred", e);
				}
			}

			if (packageName.contains(".")) {
				packageName = packageName.substring(0, packageName.lastIndexOf("."));
			}
		} while (!packageName.equals(rootPackage) && packageName.contains("."));

		if (resolvedView == null) {
			throw new Exception("Nessuna view trovata con il nome '" + requestedViewName + "'");
		}

		cache.put(cacheKey, resolvedView);

		handleResolvedValue(resolvedView, parameter, mavContainer, webRequest);
		return resolvedView;
	}

	protected void handleResolvedValue(Object arg, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
		webRequest.setAttribute(RESOLVED_VIEW_REQUEST_ATTRIBUTE, arg, RequestAttributes.SCOPE_REQUEST);
	}
}
