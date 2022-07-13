package com.hotlist.controller;

import com.hotlist.common.R;
import com.hotlist.common.dto.HotSiteDto;
import com.hotlist.common.dto.SiteShowSchemaDto;
import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.common.vo.UserVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.ManagerService;
import com.hotlist.service.UserService;
import com.hotlist.utils.HotContext;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/u")
public class ManagerController {
    @Resource
    private UserService userService;
    @Resource
    private ManagerService managerService;

    @GetMapping("/mysite")
    public R getMySite() {
        List<HotSiteEntity> mySite = managerService.getMySite();
        return R.ok().put("data", mySite);
    }

    @GetMapping("/mySiteCard")
    public R getMySiteResourceCard() {
        List<HotCardSiteWrapperVo> mySiteResourceCard = managerService.getMySiteResourceCard();
        return R.ok().put("data", mySiteResourceCard);
    }

    @PostMapping("/saveSite")
    public R saveSite(@RequestBody HotSiteDto hotSiteDto) {
        HotSiteEntity hotSite = new HotSiteEntity();
        BeanUtils.copyProperties(hotSiteDto, hotSite);
        hotSite.saveBySite();
        return R.ok();
    }

    @PostMapping("/delSite")
    public R delSite(@RequestBody HotSiteDto hotSiteDto) {
        HotSiteEntity hotSite = new HotSiteEntity();
        BeanUtils.copyProperties(hotSiteDto, hotSite);
        hotSite.delSite();
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
        BeanUtils.copyProperties(HotContext.getCurrentUser(), userVo);
        return R.ok().put("data", userVo);
    }

}
