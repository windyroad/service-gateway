package au.com.windyroad.hateoas.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.util.StdConverter;

import au.com.windyroad.hateoas.core.entities.Entity;
import au.com.windyroad.servicegateway.model.AdminRootController;

@Component
public class EntityWrapperConverter
        extends StdConverter<AdminRootController, AdminRootController> {

    @Override
    public AdminRootController convert(AdminRootController value) {
        Enhancer e = new Enhancer();
        e.setClassLoader(this.getClass().getClassLoader());
        e.setSuperclass(AdminRootController.class);
        e.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args,
                    MethodProxy proxy) throws Throwable {

                if (method.getName().equals("getAction")) {
                    return proxy.invokeSuper(obj, args);
                } else {
                    Map<String, Object> context = new HashMap<>();

                    Parameter[] params = method.getParameters();
                    for (int i = 0; i < params.length; ++i) {
                        context.put(params[i].getName(), args[i]);
                    }

                    Action<?> action = ((AdminRootController) obj)
                            .getAction(method.getName());
                    if (action == null) {
                        throw new RuntimeException(
                                "The method `" + method.getName()
                                        + "` cannot be executed remotely");
                    } else {
                        @SuppressWarnings("unchecked")
                        CompletableFuture<Entity> xxxResult = (CompletableFuture<Entity>) action
                                .invoke(context);
                        return xxxResult;
                    }
                }
            }
        });
        AdminRootController myProxy = (AdminRootController) e.create();
        return myProxy;
    }

}
