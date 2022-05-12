package net.curso.springbootmvc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplUserDetailsService implUserDetailsService;

	@Override //CONFIGURA AS SOLICITAÇÕES DE ACESSO POR HTTP
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable() //DESATIVA AS CONFIGURAÇÕES PADRÃO DE MEMÓRIA.
		.authorizeRequests() //PERMITIR RESTRINGIR ACESSOS
		.antMatchers(HttpMethod.GET, "/").permitAll() //QUALQUER USUÁRIO ACESSA A PAGINA INICIAL
		.antMatchers(HttpMethod.GET, "/cadastropessoas").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //PERMITE QUALQUER USUÁRIO
		.loginPage("/login") //BUSCA PAGINA DE LOGIN /login
		.defaultSuccessUrl("/cadastropessoas")
		.failureUrl("/login?error=true")
		.and().logout().logoutSuccessUrl("/login") //MAPEIA URL DE LOGOUT E INVALIDA USUÁRIO AUTENTICADO
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override //CRIA AUTENTICAÇÃO DO USUÁRIO COM BANCO DE DADOS OU EM MEMÓRIA
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	
		auth.userDetailsService(implUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		/*auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		.withUser("jhonata")
		.password("123")
		.roles("ADMIN");*/
	}
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		web.ignoring().antMatchers("/bootstrap/**");
		//web.ignoring().antMatchers("/cadastro/**");
	}
}
