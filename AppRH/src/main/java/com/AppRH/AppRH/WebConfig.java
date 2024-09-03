package com.AppRH.AppRH;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("FuncionarioHR@gmail.com").password("{noop}12345").roles("FUNCIONARIO")
            .and()
            .withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/cadastrarFuncionario").hasRole("ADMIN") // Acesso apenas para ADMIN
                .antMatchers("/funcionarios").hasRole("ADMIN") // Acesso apenas para ADMIN
               
                
                .antMatchers("/vagas", "/cadastrarVaga").hasRole("FUNCIONARIO") // Acesso para FUNCIONARIO
                .antMatchers("/**").authenticated() // Requer autenticação para todas as outras páginas
                .anyRequest().denyAll() // Nega acesso a todas as outras rotas não especificadas
            .and()
            .formLogin()
                .loginPage("/login")
                .successHandler(customSuccessHandler()) // Usa um handler personalizado para redirecionamento após login
                .permitAll()
            .and()
            .logout()
                .permitAll()
            .and()
            .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/bootstrap/**", "/image/**"); // Ignora as permissões de segurança para recursos estáticos
    }

    // Método para criar um handler personalizado para redirecionamento
    private AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/cadastrarFuncionario");
            } else if (role.equals("ROLE_FUNCIONARIO")) {
                response.sendRedirect("/");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}
