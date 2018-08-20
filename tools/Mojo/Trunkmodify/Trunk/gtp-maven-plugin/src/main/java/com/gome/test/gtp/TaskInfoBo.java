package com.gome.test.gtp;


import com.gome.test.gtp.dao.TaskInfoDao;
import com.gome.test.gtp.dao.TaskListDao;
import com.gome.test.gtp.jenkins.TaskStatusBo;
import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.schedule.TaskInfoListBo;
import com.gome.test.gtp.utils.Constant;
import org.apache.maven.plugin.MojoFailureException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskInfoBo {
    private TaskStatusBo taskStatusBo;
    private TaskListDao taskListDao;
    private TaskInfoDao taskInfoDao;
    private TaskInfoListBo taskInfoListBo;

    public TaskInfoBo() {
        taskStatusBo = (TaskStatusBo) Application.getBean(TaskStatusBo.class);
        taskListDao = (TaskListDao) Application.getBean(TaskListDao.class);
        taskInfoDao = (TaskInfoDao) Application.getBean(TaskInfoDao.class);
        taskInfoListBo = (TaskInfoListBo) Application.getBean(TaskInfoListBo.class);
    }

    public void updateTaskStatus(int taskListId, int origianlStatus, int goalStatus, String log) throws MojoFailureException {
        try {
            taskStatusBo.updateTaskStatus(taskListId, origianlStatus, goalStatus, "gtp maven plugin", log);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MojoFailureException(ex.getMessage());
        }
    }

    public TaskList getTaskListByIdAndCheck(int taskListId, int origianlStatus) throws MojoFailureException {
        try {
            return taskStatusBo.getTaskListByIdAndCheck(taskListId, origianlStatus);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MojoFailureException(ex.getMessage());
        }
    }

    public TaskList getTaskListById(int taskListId) throws MojoFailureException {
        try {
            return taskStatusBo.getTaskListById(taskListId);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MojoFailureException(ex.getMessage());
        }
    }




    public int getRealStatusByTaskListId(int taskListId) {
        List<TaskList> taskLists = taskListDao.getSplitedTaskListById(taskListId);
        Set<Integer> statusSet = new HashSet<Integer>();
        for(TaskList taskList : taskLists) {
            statusSet.add(taskList.getTaskState());
        }

        if (taskStatusBo.isAllEndStatus(statusSet)) {
            return taskStatusBo.getEndStatus(statusSet);
        } else {
            return Constant.NON_END_STATUS;
        }
    }


    public synchronized void endCheck(int taskListId) throws Exception {
        TaskList taskList = taskStatusBo.getTaskListById(taskListId);
        if (taskList != null && !taskStatusBo.isEndStatus(taskList.getTaskState())) {
            taskInfoListBo.amendListInfoAgent(taskList, Constant.JOB_ERROR, "[EndCheck]", Constant.JOB_ERROR);
        }
    }
}
