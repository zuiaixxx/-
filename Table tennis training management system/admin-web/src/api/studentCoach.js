import request from '@/utils/request'

/**
 * 学生教练关系API接口
 */

/**
 * 保存学生教练关系
 * @param {Object} data - 学生教练关系数据（StudentCoach对象）
 * @param {number} data.userId - 学生用户ID
 * @param {number} data.coachId - 教练ID
 * @returns {Promise} 返回保存结果
 */
export function saveStudentCoach(data) {
    return request({
        url: '/studentCoach/save',
        method: 'post',
        data
    })
}

/**
 * 获取我的教练列表
 * @returns {Promise} 返回当前学生的教练列表（CoachDetail[]）
 */
export function getMyCoach() {
    return request({
        url: '/studentCoach/getMyCoach',
        method: 'get'
    })
}

/**
 * 获取我的学生列表
 * @returns {Promise} 返回当前教练的学生列表（User[]）
 */
export function getMyStudent() {
    return request({
        url: '/studentCoach/getMyStudent',
        method: 'get'
    })
}