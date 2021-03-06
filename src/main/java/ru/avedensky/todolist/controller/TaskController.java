package ru.avedensky.todolist.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avedensky.todolist.model.Task;
import ru.avedensky.todolist.service.TaskService;

import java.util.List;

/**
 * Created by alexey on 01.05.17.
 */

@RestController
//@Controller
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private TaskService taskService;

    @Autowired(required = true)
    @Qualifier(value = "taskService")
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET LIST OF TASK
     *
     * @return JSON Task Object
     */
    @RequestMapping(value = "tasks", method = RequestMethod.GET)
    public ResponseEntity<List<Task>> listTasks() {
        logger.info("List all tasks");
        List<Task> tasks = this.taskService.listTasks();
        if (tasks.isEmpty()) {
            return new ResponseEntity<List<Task>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }

    /**
     * GET TASK BY ID
     *
     * @param id indentification of task
     * @return Return JSON Object task by id
     */
    @RequestMapping(value = "/task/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> getTask(@PathVariable("id") int id) {
        logger.info("Get Task by id: " + id);
        Task task = this.taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    /**
     * ADD
     *
     * Example JSON {"description":"This is task","id":0,"date":1493212282000,"hasdone":false}
     *
     * @param task geted task object
     * @return Http status
     */
    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public ResponseEntity<Task> createTask(@RequestBody Task task) { //,    UriComponentsBuilder ucBuilder) {
        logger.info("Add new Task to BD");

        if (task.getId() == 0) {
            int id = this.taskService.addTask(task);

            Task storedTask = this.taskService.getTaskById(id);
            if (storedTask == null) {
                return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Task>(task, HttpStatus.OK);

        } else {
            this.taskService.updateTask(task);
            return new ResponseEntity<Task>(HttpStatus.CONFLICT);
        }
    }

    /**
     * UPDATE TASK BY ID
     *
     * @param id   task indentification
     * @param task geted task object
     * @return Http status
     */
    @RequestMapping(value = "/task/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Task> updateTask(@PathVariable("id") int id, @RequestBody Task task) {
        logger.info("Update Task by id: " + id);

        Task findedTask = this.taskService.getTaskById(id);

        if (findedTask == null) {
            logger.info("Task with id: " + id + " not found");
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }

        findedTask.setDescription(task.getDescription());
        findedTask.setDate(task.getDate());
        findedTask.setHasDone(task.getHasDone());
        this.taskService.updateTask(findedTask);

        return new ResponseEntity<Task>(findedTask, HttpStatus.OK);
    }

    /**
     * DELETE TASK BY ID
     *
     * @param id task indentification
     * @return Http status
     */
    @RequestMapping(value = "/task/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Task> deleteTask(@PathVariable("id") int id) {
        logger.info("Deleting Task by id: " + id);

        Task findedTask = this.taskService.getTaskById(id);

        if (findedTask == null) {
            logger.info("Can't to delete. Task with id " + id + " not found");
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }

        this.taskService.removeTask(id);
        return new ResponseEntity<Task>(HttpStatus.NO_CONTENT);
    }

}
