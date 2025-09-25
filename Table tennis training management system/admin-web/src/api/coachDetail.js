import request from '@/utils/request'

/**
 * 获取教练详情列表（分页查询）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.username - 用户名（教练用户名）
 * @param {string} params.level - 教练等级
 * @param {string} params.campusId - 所属校区编号
 * @returns {Promise} 返回教练详情分页数据
 */
export function getCoachDetailList(params) {
    return request({
        url: '/coachDetail/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取教练详情
 * @param {number} id - 教练详情ID
 * @returns {Promise} 返回教练详情数据
 */
export function getCoachDetailById(id) {
    return request({
        url: `/coachDetail/getById/${id}`,
        method: 'get'
    })
}

/**
 * 根据用户名获取教练详情
 * @param {string} username - 教练用户名
 * @returns {Promise} 返回教练详情数据
 */
export function getCoachDetailByUsername(username) {
    return request({
        url: `/coachDetail/getByUsername/${username}`,
        method: 'get'
    })
}

/**
 * 删除教练详情
 * @param {number} id - 教练详情ID
 * @returns {Promise} 返回删除结果
 */
export function deleteCoachDetail(id) {
    return request({
        url: `/coachDetail/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新教练详情信息
 * @param {Object} coachDetail - 教练详情数据
 * @param {string} coachDetail.username - 教练用户名（逻辑外键）
 * @param {string} coachDetail.realName - 真实姓名
 * @param {string} coachDetail.level - 教练等级（初级、中级、高级）
 * @param {number} coachDetail.hourlyRate - 每小时收费
 * @param {string} coachDetail.photoUrl - 教练照片URL
 * @param {string} coachDetail.achievements - 比赛成绩描述
 * @param {string} coachDetail.status - 审核状态（pending, approved, rejected）
 * @param {string} coachDetail.campusId - 所属校区编号（逻辑外键）
 * @returns {Promise} 返回保存结果
 */
export function saveCoachDetail(coachDetail) {
    return request({
        url: '/coachDetail/save',
        method: 'post',
        data: coachDetail
    })
}

/**
 * 更新教练详情信息
 * @param {Object} coachDetail - 教练详情数据（包含ID）
 * @returns {Promise} 返回更新结果
 */
export function updateCoachDetail(coachDetail) {
    return request({
        url: '/coachDetail/save',
        method: 'post',
        data: coachDetail
    })
}

/**
 * 批量删除教练详情
 * @param {Array} ids - 教练详情ID数组
 * @returns {Promise} 返回删除结果
 */
export function batchDeleteCoachDetail(ids) {
    const deletePromises = ids.map(id => deleteCoachDetail(id))
    return Promise.all(deletePromises)
}