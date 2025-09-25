import request from '@/utils/request'

/**
 * 预约记录API接口
 */

/**
 * 获取预约记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.bookingNo - 预约编号
 * @param {string} params.status - 预约状态
 * @param {string} params.campusId - 校区ID
 * @returns {Promise} 返回分页数据
 */
export function getBookingRecordList(params) {
    return request({
        url: '/bookingRecord/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取预约记录详情
 * @param {number} id - 预约记录ID
 * @returns {Promise} 返回预约记录详情
 */
export function getBookingRecordById(id) {
    return request({
        url: `/bookingRecord/getById/${id}`,
        method: 'get'
    })
}

/**
 * 删除预约记录
 * @param {number} id - 预约记录ID
 * @returns {Promise} 返回删除结果
 */
export function deleteBookingRecord(id) {
    return request({
        url: `/bookingRecord/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新预约记录
 * @param {Object} data - 预约记录数据
 * @param {number} data.id - 记录ID（更新时需要）
 * @param {string} data.bookingNo - 预约编号
 * @param {string} data.courseId - 课程ID
 * @param {string} data.username - 用户名
 * @param {string} data.studentName - 学员姓名
 * @param {string} data.campusId - 校区ID
 * @param {string} data.status - 预约状态
 * @param {string} data.remark - 备注
 * @returns {Promise} 返回保存结果
 */
export function saveBookingRecord(data) {
    return request({
        url: '/bookingRecord/save',
        method: 'post',
        data
    })
}

/**
 * 获取我的预约记录列表
 * @returns {Promise} 返回当前教练的预约记录列表（BookingRecord[]）
 */
export function getByCoachBooking() {
    return request({
        url: '/bookingRecord/getByCoachBooking/',
        method: 'get'
    })
}