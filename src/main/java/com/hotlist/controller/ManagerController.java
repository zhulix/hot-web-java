package com.hotlist.controller;

import com.hotlist.common.R;
import com.hotlist.common.dto.HotSiteDto;
import com.hotlist.common.dto.SiteShowSchemaDto;
import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.common.vo.UserVo;
import com.hotlist.dao.HotSiteDAO;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.ManagerService;
import com.hotlist.service.UserService;
import com.hotlist.utils.HotContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/u")
public class ManagerController {
    public ManagerController(UserService userService,
                             ManagerService managerService,
                             HotSiteDAO hotSiteDAO) {
        this.userService = userService;
        this.managerService = managerService;
        this.hotSiteDAO = hotSiteDAO;
    }

    private final UserService userService;

    private final ManagerService managerService;

    private final HotSiteDAO hotSiteDAO;

    @GetMapping("/mysite")
    public R getMySite() {
        List<HotSiteEntity> mySite = managerService.getMySite(HotContext.getCurrentUser());
        return R.ok().put("data", mySite);
    }

    @GetMapping("/mySiteCard")
    public R getMySiteResourceCard() {

        List<HotCardSiteWrapperVo> mySiteResourceCard =
                managerService.getMySiteResourceCard(HotContext.getCurrentUser());
        return R.ok().put("data", mySiteResourceCard);
    }

    @PostMapping("/saveSite")
    public R saveSite(@RequestBody HotSiteDto hotSiteDto) {
        HotSiteEntity hotSite = new HotSiteEntity();
        BeanUtils.copyProperties(hotSiteDto, hotSite);
        hotSiteDAO.saveSite(hotSite, HotContext.getCurrentUser());
        return R.ok();
    }

    @PostMapping("/delSite")
    public R delSite(@RequestBody HotSiteDto hotSiteDto) {
        HotSiteEntity hotSite = new HotSiteEntity();
        BeanUtils.copyProperties(hotSiteDto, hotSite);
        hotSiteDAO.delSite(hotSite, HotContext.getCurrentUser());
        return R.ok();
    }

    @PostMapping("/showSchema")
    public R showSchema(@RequestBody SiteShowSchemaDto siteShowSchemaDto) {
        userService.update(HotContext.getCurrentUser().setShowSchema(siteShowSchemaDto.getShowSchema()));
        return R.ok();
    }

    @GetMapping("/info")
    public R info() {
        UserVo userVo = new UserVo();
        if (Objects.isNull(HotContext.getCurrentUser())) HotContext.setDefaultUser();
        BeanUtils.copyProperties(HotContext.getCurrentUser(), userVo);
        return R.ok().put("data", userVo);
    }

}
