package top.vimerzhao.simpleevnetbus;

/**
 * Created by vimerzhao on 18-12-23
 */
public class EventType {
    Class<?> paramType;

    public EventType(Class<?> paramType) {
        this.paramType = paramType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paramType== null) ? 0 : paramType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventType) {
            EventType other = (EventType) obj;
            return paramType.equals(other.paramType);
        } else {
            return false;
        }
    }
}
