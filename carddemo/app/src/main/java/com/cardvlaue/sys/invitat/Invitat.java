package com.cardvlaue.sys.invitat;

import com.cardvlaue.sys.data.ErrorResponse;
import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class Invitat extends ErrorResponse {

    private List<InvitatItem> invitees;

    private String inviteCount;

    private String amount;

    public List<InvitatItem> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<InvitatItem> invitees) {
        this.invitees = invitees;
    }

    public String getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(String inviteCount) {
        this.inviteCount = inviteCount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
