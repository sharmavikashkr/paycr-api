package com.paycr.service.setup;

import java.io.Serializable;

import com.paycr.common.util.CommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private RolePermissionService rolePermSer;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomain, Object targetId) {
        Object email = auth.getPrincipal();
        if (CommonUtil.isNull(email) || CommonUtil.isNull(auth) || CommonUtil.isNull(targetDomain)
                || CommonUtil.isNull(targetId) || !(targetDomain instanceof String) || !(targetId instanceof Integer)) {
            return false;
        }
        return rolePermSer.checkIfActionAllowed((String) email, (String) targetDomain, (Integer) targetId, "id");
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetDomain, Object column) {
        Object email = auth.getPrincipal();
        if (CommonUtil.isNull(email) || CommonUtil.isNull(auth) || CommonUtil.isNull(targetDomain)
                || CommonUtil.isNull(column) || CommonUtil.isNull(targetId) || !(targetDomain instanceof String)
                || !(targetId instanceof String) || !(column instanceof String)) {
            return false;
        }
        return rolePermSer.checkIfActionAllowed((String) email, (String) targetDomain, (String) targetId,
                (String) column);
    }
}