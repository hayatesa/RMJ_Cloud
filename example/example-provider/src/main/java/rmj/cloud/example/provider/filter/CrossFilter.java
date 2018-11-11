package rmj.cloud.example.provider.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理跨域请求
 */
@WebFilter(filterName = "crossFilter", urlPatterns = "/*")
public class CrossFilter implements Filter {

    public CrossFilter() {
        super();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) res;

        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,X-E4M-With,userId,token");

        httpServletResponse.setContentType("application/json;charset=UTF-8");

        httpServletResponse.setHeader("X-Requested-With", "XMLHttpRequest");

        httpServletResponse.setHeader("XDomainRequestAllowed", "1");

        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        httpServletResponse.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) req).getHeader("Origin"));

        httpServletResponse.setHeader("Access-Control-Allow-Headers", "accept,content-type");

        httpServletResponse.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE,PUT,PATCH");

        chain.doFilter(req, httpServletResponse);

    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
