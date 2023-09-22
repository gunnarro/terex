package com.gunnarro.android.terex.observable.event;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
public class TimesheetEvent {

    private final TimesheetEventTypeEnum eventType;
    private final TimesheetEntry timesheetEntry;

    private final String status;

    private TimesheetEvent(Builder builder) {
        this.eventType = Objects.requireNonNull(builder.eventType, "eventType");
        this.timesheetEntry = Objects.requireNonNull(builder.timesheetEntry, "timesheetEntry");
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isAdd() {
        return eventType.equals(TimesheetEventTypeEnum.ADD);
    }

    public boolean isDelete() {
        return eventType.equals(TimesheetEventTypeEnum.DELETE);
    }

    public TimesheetEntry getTimesheetEntry() {
        return timesheetEntry;
    }

    @NotNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimesheetEvent{");
        sb.append("eventType=").append(eventType);
        sb.append(", timesheetEntry=").append(timesheetEntry);
        sb.append('}');
        return sb.toString();
    }

    public enum TimesheetEventTypeEnum {
        ADD, DELETE, ACTION_STATUS;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private TimesheetEventTypeEnum eventType;
        private TimesheetEntry timesheetEntry;
        private String status;

        private Builder() {
        }

        public Builder eventType(TimesheetEventTypeEnum eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder timesheetEntry(TimesheetEntry timesheetEntry) {
            this.timesheetEntry = timesheetEntry;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder of(TimesheetEvent timesheetEvent, TimesheetEntry timesheetEntry, String status) {
            this.eventType = timesheetEvent.eventType;
            this.timesheetEntry = timesheetEntry;
            this.status = status;
            return this;
        }

        public TimesheetEvent build() {
            return new TimesheetEvent(this);
        }
    }
}
