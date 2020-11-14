package com.st.naga.server.jwt;


import com.st.naga.entity.system.User;

public class ContextUtil {
    private static ThreadLocal<User> local = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        local.set(user);
    }

    public static User getCurrentUser() {
        return local.get();
    }
}
