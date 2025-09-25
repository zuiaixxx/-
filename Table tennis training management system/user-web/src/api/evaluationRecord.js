import request from '@/utils/request'

/**
 * 评价记录API接口
 */

/**
 * 获取评价记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.bookingNo - 预约单号
 * @returns {Promise} 返回分页数据
 */
export function getEvaluationRecordList(params) {
    return request({
        url: '/evaluationRecord/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取评价记录详情
 * @param {number} id - 评价记录ID
 * @returns {Promise} 返回评价记录详情
 */
export function getEvaluationRecordById(id) {
    return request({
        url: `/evaluationRecord/getById/${id}`,
        method: 'get'
    })
}

/**
 * 删除评价记录
 * @param {number} id - 评价记录ID
 * @returns {Promise} 返回删除结果
 */
export function deleteEvaluationRecord(id) {
    return request({
        url: `/evaluationRecord/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新评价记录
 * @param {Object} data - 评价记录数据
 * @param {number} data.id - 记录ID（更新时需要）
 * @param {string} data.bookingNo - 预约单号
 * @param {string} data.studentUsername - 学员用户名
 * @param {string} data.studentName - 学员姓名
 * @param {string} data.coachUsername - 教练用户名
 * @param {string} data.coachName - 教练姓名
 * @param {number} data.rating - 评分（1-5星）
 * @param {string} data.comment - 评价内容
 * @param {string} data.reply - 教练回复
 * @param {string} data.evaluationDate - 评价日期
 * @returns {Promise} 返回保存结果
 */
export function saveEvaluationRecord(data) {
    return request({
        url: '/evaluationRecord/save',
        method: 'post',
        data
    })
}

/**
 * 批量删除评价记录
 * @param {Array} ids - 评价记录ID数组
 * @returns {Promise} 返回删除结果
 */
export function batchDeleteEvaluationRecord(ids) {
    const deletePromises = ids.map(id => deleteEvaluationRecord(id))
    return Promise.all(deletePromises)
}

/**
 * 获取学员的评价记录列表
 * @returns {Promise} 返回学员的评价记录列表
 */
export function getStudentEvaluation() {
    return request({
        url: '/evaluationRecord/getStudentEvaluation',
        method: 'get'
    })
}