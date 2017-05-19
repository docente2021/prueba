package mcuca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import mcuca.security.AccessDeniedView;
import mcuca.security.ErrorView;
import mcuca.security.LoginScreen;
import mcuca.security.SecurityUtils;

@Title("McUCA")
@Theme("dashboard")
@SuppressWarnings("serial")
@SpringUI
public class VaadinUI extends UI {

	@Autowired
	SpringViewProvider viewProvider;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
    MainScreen mainScreen;


	@Override
	protected void init(VaadinRequest request) {

	   	this.getUI().getNavigator().setErrorView(ErrorView.class);
		viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);

		if (SecurityUtils.isLoggedIn()) {
			showMainScreen();
		} else {
			showLoginScreen();
		}

	}

	private void showLoginScreen() {
		setContent(new LoginScreen(this::login));
	}

	private void showMainScreen() {
		setContent(mainScreen);
	}


	private boolean login(String username, String password) {
		try {
			Authentication token = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work with websocket communication.
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
			SecurityContextHolder.getContext().setAuthentication(token);

			// Show the main UI
			showMainScreen();
			return true;
		} catch (AuthenticationException ex) {
			return false;
		}
	}


}