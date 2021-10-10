package com.paycr.service.setup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.paycr.common.data.domain.*;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Role;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.RoleUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionService {

    @Autowired
    private MerchantUserRepository merUserRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MerchantRepository merRepo;

    @PersistenceContext
    private EntityManager em;

    public boolean checkIfActionAllowed(final String email, final String targetDomain, final Serializable targetId,
            final String column) {
        try {
            final Merchant merchant = getMerchantForUser(email);
            if (CommonUtil.isNull(merchant)) {
                return false;
            }
            final Domain domain = Domain.valueOf(targetDomain);
            Query query = null;
            if (domain.allowNull) {
                query = em.createQuery("SELECT d FROM " + domain.clazz.getName()
                        + " d WHERE (d.merchant = :merchant OR d.merchant IS NULL) AND d." + column + " = :" + column);
            } else {
                query = em.createQuery("SELECT d FROM " + domain.clazz.getName()
                        + " d WHERE d.merchant = :merchant AND d." + column + " = :" + column);
            }
            query.setParameter("merchant", merchant);
            query.setParameter(column, targetId);
            final Object result = query.getSingleResult();
            return result != null;
        } catch (final Exception ex) {
            return false;
        }
    }

    private Merchant getMerchantForUser(final String email) {
        final PcUser user = userRepo.findByEmail(email);
        final String[] roles = user.getUserRoles().stream().map(r -> r.getRole().name())
                .toArray(size -> new String[size]);
        final List<String> roleList = Arrays.asList(roles);
        if (RoleUtil.MERCHANT_ROLES.stream().anyMatch(r -> roleList.contains(r))) {
            final MerchantUser merUser = merUserRepo.findByUserId(user.getId());
            if (CommonUtil.isNull(merUser)) {
                return null;
            }
            final Optional<Merchant> merchantOpt = merRepo.findById(merUser.getMerchantId());
            return merchantOpt.isPresent() ? merchantOpt.get() : null;
        }
        return null;
    }

    private enum Domain {
        REPORT(Report.class, true), SCHEDULE(Schedule.class, false), ASSET(Asset.class, false),
        CONSUMER(Consumer.class, false), INVENTORY(Inventory.class, false), SUPPLIER(Supplier.class, false),
        INVOICE(Invoice.class, false), EXPENSE(Expense.class, false);

        Class clazz;
        boolean allowNull;

        Domain(final Class clazz, final boolean allowNull) {
            this.clazz = clazz;
            this.allowNull = allowNull;
        }
    }

}