package pici.webcontroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class ThymeleafLayoutInterceptor implements AsyncHandlerInterceptor {

    private static final String DEFAULT_LAYOUT = "default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";
    
    @Autowired
    private IndexController indexController;
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }
        String originalViewName = modelAndView.getViewName();
        if (isRedirectOrForward(originalViewName)) {
            return;
        }
        
        if(isFragment(originalViewName)) {
        	return;
        }
        
        if(!isDefault(originalViewName)) {
        	return;
        }
        
        indexController.fillIndexModel(modelAndView);

        
        
        modelAndView.setViewName(DEFAULT_LAYOUT);
        modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, originalViewName);
    } 
    
	private boolean isDefault(String viewName) {
		return viewName.startsWith("views/") ;
	}

	private boolean isFragment(String originalViewName) {
		return originalViewName.indexOf("::") > -1;
	}
	private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }   
}