// org.example.library.annotations/AnnotationProcessor.java
package org.example.library.annotations;

import org.example.library.domain.Member;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public final class AnnotationProcessor {

    private static final Pattern EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private AnnotationProcessor() {}

    // Prints @Version info at startup
    public static void printVersionInfo(Class<?>... classes) {
        for (Class<?> c : classes) {
            Version v = c.getAnnotation(Version.class);
            if (v != null) {
                System.out.println("[VERSION] " + c.getSimpleName()
                        + " v" + v.major() + "." + v.minor()
                        + " | author=" + v.author());
            }
        }
    }

    /**
     * Proxy that:
     * - logs calls to methods annotated with @Audited
     * - validates parameters annotated with @ValidMember
     *
     * IMPORTANT: Java dynamic proxy works with interfaces.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createAuditedProxy(Class<T> iface, T target) {
        if (!iface.isInterface()) throw new IllegalArgumentException("iface must be an interface");

        InvocationHandler handler = (proxy, method, args) -> {
            Method implMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

            validateParameters(implMethod, args);

            Audited audited = implMethod.getAnnotation(Audited.class);
            if (audited != null) {
                System.out.println("[AUDIT] action=" + audited.action()
                        + " method=" + target.getClass().getSimpleName() + "." + implMethod.getName()
                        + " args=" + (args == null ? "[]" : Arrays.toString(args)));
            }

            return implMethod.invoke(target, args);
        };

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                handler
        );
    }

    private static void validateParameters(Method implMethod, Object[] args) {
        Annotation[][] anns = implMethod.getParameterAnnotations();
        if (anns.length == 0) return;

        for (int i = 0; i < anns.length; i++) {
            for (Annotation a : anns[i]) {
                if (a.annotationType() == ValidMember.class) {
                    Object val = (args == null ? null : args[i]);

                    if (val == null) throw new IllegalArgumentException("Member is null (@ValidMember)");
                    if (!(val instanceof Member))
                        throw new IllegalArgumentException("@ValidMember must be on Member parameter");

                    Member m = (Member) val;
                    String email = m.getEmail();
                    if (email == null || !EMAIL.matcher(email).matches()) {
                        throw new IllegalArgumentException("Invalid member email (@ValidMember): " + email);
                    }
                }
            }
        }
    }
}