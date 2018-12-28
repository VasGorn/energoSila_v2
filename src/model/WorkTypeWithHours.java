package model;

public class WorkTypeWithHours {
    private WorkType workType;
    private int hoursOnWorkType;

    public WorkTypeWithHours(WorkType workType, int hoursOnWorkType){
        this.workType = workType;
        this.hoursOnWorkType = hoursOnWorkType;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public int getHoursOnWorkType() {
        return hoursOnWorkType;
    }

    public void setHoursOnWorkType(int hoursOnWorkType) {
        this.hoursOnWorkType = hoursOnWorkType;
    }
}
