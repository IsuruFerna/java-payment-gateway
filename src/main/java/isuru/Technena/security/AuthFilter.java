package isuru.Technena.security;

import io.jsonwebtoken.ExpiredJwtException;
import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.NotFoundException;
import isuru.Technena.exceptions.UnauthorizedException;
import isuru.Technena.repository.UserRepo;
import isuru.Technena.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Insert the token in the authorization header");
            } else {
                String accessToken = authHeader.substring(7);

                // verify token
                jwtTools.verifyToken(accessToken);

                String id = jwtTools.extractIdFromToken(accessToken);
                User user = userService.findById(UUID.fromString(id));

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } catch (ExpiredJwtException | NotFoundException | UnauthorizedException ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] allowedPaths = {"/auth/**"};

        return Stream.of(allowedPaths)
                .anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
    }
}
