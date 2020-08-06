package com.immomo.mts.flow.limit.http.service.impl;

import com.immomo.mcf.util.LogUtils;
import com.immomo.mts.flow.limit.http.domain.CallSourceInfo;
import com.immomo.mts.flow.limit.http.mapper.CallSourceInfoMapper;
import com.immomo.mts.flow.limit.http.service.CallSourceInfoService;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : tao.kaili
 * @description :
 * @date : 2019/11/4 下午5:47
 */
@Service
public class CallSourceInfoServiceImp implements CallSourceInfoService {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    @Autowired
    private CallSourceInfoMapper callSourceInfoMapper;

    @Override
    public boolean add(CallSourceInfo callSourceInfo) {
        try {
            callSourceInfoMapper.save(callSourceInfo);

            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "callsource add failed {0}", callSourceInfo.toString());
            return false;
        }

    }

    @Override
    public boolean delete(String groupName, String callSource) {
        try {
            callSourceInfoMapper.delete(groupName, callSource);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "callsource delete failed groupName={0},callSource={1}", groupName, callSource);
            return false;
        }
    }

    @Override
    public List<CallSourceInfo> queryCallSourceInfoByPage(int start, int limitSize) {

        return callSourceInfoMapper.queryCallSourceInfoByPage(start, limitSize);
    }

    @Override
    public List<CallSourceInfo> getCallSourceInfoByGroupName(String groupName) {
        return callSourceInfoMapper.getCallSourceByGroupName(groupName);
    }

    @Override
    public List<CallSourceInfo> getCallSourcesByGroupName(String groupName, String callSource) {
        return callSourceInfoMapper.searchGroupNameAndCallSource(groupName, callSource);
    }

    @Override
    public Integer batchAdd(List<CallSourceInfo> callSourceInfoList) {
        Integer addSize = 0;
        for (CallSourceInfo callSourceInfo : callSourceInfoList) {
            try {
                boolean success = add(callSourceInfo);
                if (success) {
                    addSize++;
                }
            } catch (Exception e) {
                LogUtils.error(LOGGER, e, "add error callsourceinfo error. |{0}|", callSourceInfo);
            }
        }
        return addSize;
    }

    @Override
    public boolean updateDescInfo(String descInfo, String groupName, String callSource) {
        try {
            callSourceInfoMapper.updateDescInfo(descInfo, groupName, callSource);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "update error updateDescInfo error {0},{1},{2}", descInfo, groupName, callSource);
            return false;
        }

    }

    @Override
    public CallSourceInfo getOneCallInfo(String groupName, String callSource) {
        try {
            return callSourceInfoMapper.getOneCallInfo(groupName, callSource);
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "getOneCallInfo error {0},{1}", groupName, callSource);
            return null;
        }
    }
}
