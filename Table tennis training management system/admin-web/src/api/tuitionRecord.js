import request from '@/utils/request'

/**
 * 缴费记录API接口
 */

/**
 * 获取缴费记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.recordNo - 记录编号
 * @param {string} params.paymentMethod - 支付方式
 * @returns {Promise} 返回分页数据
 */
export function getTuitionRecordList(params) {
    return request({
        url: '/tuitionRecord/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取缴费记录详情
 * @param {number} id - 缴费记录ID
 * @returns {Promise} 返回缴费记录详情
 */
export function getTuitionRecordById(id) {
    return request({
        url: `/tuitionRecord/getById/${id}`,
        method: 'get'
    })
}

/**
 * 删除缴费记录
 * @param {number} id - 缴费记录ID
 * @returns {Promise} 返回删除结果
 */
export function deleteTuitionRecord(id) {
    return request({
        url: `/tuitionRecord/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新缴费记录
 * @param {Object} data - 缴费记录数据
 * @param {number} data.id - 记录ID（更新时需要）
 * @param {string} data.recordNo - 记录编号
 * @param {string} data.paymentMethod - 支付方式
 * @param {number} data.amount - 缴费金额
 * @param {string} data.studentUsername - 学员用户名
 * @param {string} data.remark - 备注
 * @returns {Promise} 返回保存结果
 */
export function saveTuitionRecord(data) {
    return request({
        url: '/tuitionRecord/save',
        method: 'post',
        data
    })
}