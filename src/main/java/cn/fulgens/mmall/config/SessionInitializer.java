package cn.fulgens.mmall.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * SpringSession初始化器
 * Extending AbstractHttpSessionApplicationInitializer ensures that
 * the Spring Bean by the name of springSessionRepositoryFilter is registered
 * with our Servlet Container for every request.
 *
 * @author fulgens
 */
public class SessionInitializer extends AbstractHttpSessionApplicationInitializer {

}
