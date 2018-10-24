package com.example.rator.controllers;

import com.example.rator.models.Evaluation;
import com.example.rator.models.ProfCourse;
import com.example.rator.models.Professor;
import com.example.rator.repositories.EvaluationRepository;
import com.example.rator.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ProfessorController {

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    EvaluationRepository evaluationRepository;

    //Creates with professor with empty evaluation in prof course
    @RequestMapping(value = "/professors", method = RequestMethod.POST)
    public ResponseEntity<Professor> createProfessor(@RequestBody Professor professor) {
        professorRepository.save(professor);
        Professor prof = professorRepository.findAllById(professor.getId());
        //Sets courseCode and profId to newly created evaluation
        for(ProfCourse course : prof.getCoursesTaught()) {
            Evaluation evaluation = new Evaluation(course.getCourseCode(), prof.getId(), 0,
                    0, 0, 0, 0, 0, 0);
            course.setEvaluation(evaluation);
            evaluationRepository.save(evaluation);
        }
        professorRepository.save(prof);
        return new ResponseEntity<Professor>(prof, HttpStatus.CREATED);
    }

    //Retrieve all professors
    @RequestMapping(value = "/professors", method = RequestMethod.GET)
    public ResponseEntity<List<Professor>> getAllProfessors() {
        List<Professor> professors = professorRepository.findAll();
        return new ResponseEntity<List<Professor>>(professors, HttpStatus.OK);
    }

    //Retrieve single professor
    @RequestMapping(value = "/professors/{professorId}", method = RequestMethod.GET)
    public ResponseEntity<Professor> getProfessor(@PathVariable("professorId") String id) {
        Professor professor = professorRepository.findAllById(id);
        return new ResponseEntity<Professor>(professor, HttpStatus.OK);
    }

    //Retrieve professors by course code
    //course code is case sensitive
    @RequestMapping(value = "/professors/course/{courseCode}", method = RequestMethod.GET)
    public ResponseEntity<List<Professor>> getAllProfessorsByCourseCode(@PathVariable("courseCode")
                                                                                    String courseCode) {
        //Return prof object with just the course associated with course code ??

        List<Professor> professorsByCourseCode = professorRepository.findByCoursesTaughtCourseCodeIgnoreCase(courseCode);
        return new ResponseEntity<List<Professor>>(professorsByCourseCode, HttpStatus.OK);
    }

    @RequestMapping(value = "/evaluations", method = RequestMethod.GET)
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        return new ResponseEntity<List<Evaluation>>(evaluations, HttpStatus.OK);
    }
    //Retrieve evaluation based on profId and courseCode
    @RequestMapping(value = "/evaluation/{profId}/{courseCode}", method = RequestMethod.GET)
    public ResponseEntity<Evaluation> getEvaluation(@PathVariable("profId") String profId,
                                                    @PathVariable("courseCode") String code) {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        for(Evaluation eval : evaluations) {
            if(eval.getCourseCode().equals(code) && eval.getProfessorId().equals(profId)) {
                return new ResponseEntity<Evaluation>(eval, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Evaluation>(HttpStatus.BAD_REQUEST);
    }
}
