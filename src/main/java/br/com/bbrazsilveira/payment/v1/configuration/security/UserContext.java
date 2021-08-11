package br.com.bbrazsilveira.payment.v1.configuration.security;

import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import lombok.NonNull;

public class UserContext {

    private static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(@NonNull User currentUser) {
        UserContext.currentUser.set(currentUser);
    }

    @NonNull
    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
