import request from '@/utils/request'

/**
 * 获取校区列表（分页查询）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.keyword - 关键词（校区名称、地址、联系人）
 * @param {string} params.campusType - 校区类型
 * @returns {Promise} 返回校区分页数据
 */
export function getCampusList(params) {
    return request({
        url: '/campus/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取校区详情
 * @param {number} id - 校区ID
 * @returns {Promise} 返回校区详情数据
 */
export function getCampusById(id) {
    return request({
        url: `/campus/getById/${id}`,
        method: 'get'
    })
}

/**
 * 删除校区
 * @param {number} id - 校区ID
 * @returns {Promise} 返回删除结果
 */
export function deleteCampus(id) {
    return request({
        url: `/campus/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新校区信息
 * @param {Object} campus - 校区数据
 * @param {string} campus.campusName - 校区名称
 * @param {string} campus.campusType - 校区类型
 * @param {string} campus.address - 地址
 * @param {string} campus.contactPerson - 联系人
 * @param {string} campus.contactPhone - 联系电话
 * @param {string} campus.description - 描述
 * @returns {Promise} 返回保存结果
 */
export function saveCampus(campus) {
    return request({
        url: '/campus/save',
        method: 'post',
        data: campus
    })
}

/**
 * 更新校区信息
 * @param {Object} campus - 校区数据（包含ID）
 * @returns {Promise} 返回更新结果
 */
export function updateCampus(campus) {
    return request({
        url: '/campus/save',
        method: 'post',
        data: campus
    })
}

/**
 * 批量删除校区
 * @param {Array} ids - 校区ID数组
 * @returns {Promise} 返回删除结果
 */
export function batchDeleteCampus(ids) {
    const deletePromises = ids.map(id => deleteCampus(id))
    return Promise.all(deletePromises)
}