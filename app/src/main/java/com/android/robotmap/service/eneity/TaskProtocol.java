package com.android.robotmap.service.eneity;


import java.io.Serializable;

public class TaskProtocol implements Serializable {
    private int type;
    private TaskInfo task;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TaskInfo getTask() {
        return task;
    }

    public void setTask(TaskInfo task) {
        this.task = task;
    }
}