package com.lb.scala.base;

import java.math.BigDecimal;

/**
 * @ClassName UDfTools
 * @Description @TODO
 * @Author liubing
 * @Date 2020/4/26 16:51
 * @Version 1.0
 **/
public class UDfTools {


    /**
     * 计算加法
     *
     * @param num
     * @return
     */
    public static BigDecimal BigDecimalAddition(String... num) {
        BigDecimal b = new BigDecimal(0);
        for (String s : num) {
            b = b.add(BigDecimal.valueOf(Double.valueOf(s)));
        }
        return b;
    }


    /**
     * 计算本笔贷款状态
     *
     * @param overdueStatus      逾期天数
     * @param period             总期数
     * @param phase              当期
     * @param is_early_repayment 是否提前结清
     * @return 1-正常 2-逾期 3-结清 5-最后一期发生代偿或者中间期债权打包卖出
     */
    public static String loanStatusDef(int overdueStatus, int period, int phase, int is_early_repayment) {
        // 判断逾期天数是否大于0
        if (overdueStatus > 0) { // 大于0
            return "2";
        } else {  // 小于0
            // 判断当前期数是否最后一期 或者  是否提前结清为是(1)
            if (period == phase || is_early_repayment == 1) {
                return "3";
            } else {
                return "1";
            }
        }
    }


    /**
     * 贷款余额
     *
     * @param amount                总本金
     * @param repaid_principal_send 实收表中期序小于等于当期期序实还上报本金之和[sum(repaid_principal_send)]
     * @return
     */
    public static String remainingAmountDef(String amount, String... repaid_principal_send) {
        return BigDecimal.valueOf(Double.valueOf(amount)).subtract(BigDecimalAddition(repaid_principal_send)).toString();
    }


    /**
     * 当前逾期总额
     *
     * @param targetRepayment 本期剩余应还款金额
     * @param realRepayment   本次还款金额
     * @return
     */
    public static String overdueAmountDef(String targetRepayment, String realRepayment) {
        return BigDecimal.valueOf(Double.valueOf(targetRepayment)).subtract(BigDecimal.valueOf(Double.valueOf(realRepayment))).toString();
    }


    /**
     * 本期还款状态确认时间
     *
     * @param status      逾期状态
     * @param due_date    逾期账单时间
     * @param repaid_time 还款时间
     * @return
     */
    public static String statusConfirmAtDef(String status, String due_date, String repaid_time) {
        // 如果状态是逾期，则取逾期账单日后一天
        if (status == "") {
            return "";
        } else {
            // 如果是还款则取还款时间[SELECT create_time FROM crc_asset_finance_paid_up_new]
            return repaid_time;
        }
    }

    // 当前逾期天数

    /**
     * 1.如果是账期内，用跑数日期-最早逾期一期的账单日；最早逾期一期账单日逻辑：
     * 应收表中账单日<上报日期且还款列表状态是'over_due' 或者（还款方式是逾期还清且实际还款时间>本次上报日）；
     （SELECT min(due_date) from crc_asset_repay_plan_new WHERE due_date<'跑数日期'
     and status='OVER_DUE' or (repaid_type='OVER_DUE_REPAY' and actual_repaid_date>'跑数日期')）
     2.如果是超过最后一期账单日，用跑数日期-最后一期账单日+最后一期账单日那条记录中的逾期天数；
     * @return
     */
    public static String overdueStatusdef(){

      return "";
    }
    /**
     * 本次还款金额
     * @param repaid_interest_send  本金
     * @param repaid_principal_send   利息
     * @param repaid_phase_servivce_company_send  服务费
     * @return
     */
    public static String realRepaymentDef(String repaid_interest_send, String repaid_principal_send, String repaid_phase_servivce_company_send){
        return BigDecimalAddition(repaid_interest_send, repaid_principal_send, repaid_phase_servivce_company_send).toString();
    }


    public static void main(String[] args) {
        System.out.println(BigDecimalAddition("10.34", "30.21", "40.09"));
        System.out.println(remainingAmountDef("100", "10.34", "30.21", "40.09"));
    }
}
