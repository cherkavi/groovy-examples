package com.wirecard.brand.job.migrationgui.mvc

import com.wirecard.brand.job.migrationgui.service.FileService
import com.wirecard.brand.job.migrationgui.service.FileSystemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

@Controller
// https://github.com/melix/springboot-groovytemplates
class MigrationJobExecutor {

    @Autowired
    FileSystemService service

    @RequestMapping(value="/", method = RequestMethod.GET)
    def home(HttpServletRequest e){
        homeView(getId(e))
    }

    @RequestMapping(value="/clear", method = RequestMethod.GET)
    def deleteFiles(HttpServletRequest e){
        def id = getId(e)
        service.clearFiles(id)
        homeView(id)
    }

    @RequestMapping(value="/upload", method = RequestMethod.POST)

    def saveFiles(@RequestParam("file-cardfulfillment") MultipartFile fileFullfillment,
                  @RequestParam("file-product") MultipartFile fileProduct,
                  HttpServletRequest e){
        def id = getId(e)
        service.saveFulfillment(id, fileFullfillment)
        service.saveFileProduct(id, fileProduct)
        homeView(id)
    }

    def homeView(address){
        new ModelAndView('view/home',
                [remoteAddress: address,
                fileData : service.getFileData(address),
                fileFulfill: service. getFileFulfill(address)])
    }

    def getId(HttpServletRequest request){
        request.getRemoteAddr().replace(".", "-")
    }

    @ExceptionHandler(IOException.class)
    def readWriteException(IOException ex){
        println(ex.getMessage())
        new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
