// org.example.library.annotations/AnnotationProcessor.java
package org.example.library.annotations;

import org.example.library.domain.Member;
import org.example.library.exceptions.InvalidMemberException;

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

            // ✅ validate using BOTH interface method + implementation method annotations
            validateParameters(method, implMethod, args);

            // ✅ audited can be on interface OR implementation
            Audited audited = implMethod.getAnnotation(Audited.class);
            if (audited == null) audited = method.getAnnotation(Audited.class);

            if (audited != null) {
                System.out.println("[AUDIT] action=" + audited.action()
                        + " method=" + target.getClass().getSimpleName() + "." + implMethod.getName()
                        + " args=" + (args == null ? "[]" : Arrays.toString(args)));
            }

            try {
                return implMethod.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause(); // unwrap
            }
        };

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                handler
        );
    }

    private static void validateParameters(Method ifaceMethod, Method implMethod, Object[] args) {
        Annotation[][] ifaceAnns = ifaceMethod.getParameterAnnotations();
        Annotation[][] implAnns = implMethod.getParameterAnnotations();

        int paramCount = Math.max(ifaceAnns.length, implAnns.length);
        if (paramCount == 0) return;

        for (int i = 0; i < paramCount; i++) {
            boolean validMember =
                    hasAnnotation(ifaceAnns, i, ValidMember.class) ||
                            hasAnnotation(implAnns, i, ValidMember.class);

            if (!validMember) continue;

            Object val = (args == null ? null : args[i]);
            if (!(val instanceof Member m)) {
                throw new InvalidMemberException();
            }

            String id = m.getMemberId();
            String name = m.getName();
            String email = m.getEmail();

            if (isBlank(id) || isBlank(name) || isBlank(email)) {
                throw new InvalidMemberException();
            }

            if (!EMAIL.matcher(email.trim()).matches()) {
                throw new InvalidMemberException();
            }
        }
    }

    private static boolean hasAnnotation(Annotation[][] anns, int idx, Class<? extends Annotation> type) {
        if (anns == null || idx < 0 || idx >= anns.length) return false;
        for (Annotation a : anns[idx]) {
            if (a.annotationType() == type) return true;
        }
        return false;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}