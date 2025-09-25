import request from '@/utils/request'

/**
 * 获取所有可预约的课程列表
 * @returns {Promise} 返回所有可预约课程数据
 */
export function getAllAvailableCourses() {
    return request({
        url: '/course/list',
        method: 'get',
        params: {
            pageNum: 1,
            pageSize: 1000,
            available: true
        }
    })
}

/**
 * 获取课程排班列表（分页查询）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.coachUsername - 教练用户名
 * @param {string} params.campusId - 校区编号
 * @returns {Promise} 返回课程排班分页数据
 */
export function getCourseList(params) {
    return request({
        url: '/course/list',
        method: 'get',
        params
    })
}

/**
 * 根据ID获取课程排班详情
 * @param {number} id - 课程排班ID
 * @returns {Promise} 返回课程排班详情数据
 */
export function getCourseById(id) {
    return request({
        url: `/course/getById/${id}`,
        method: 'get'
    })
}

/**
 * 删除课程排班
 * @param {number} id - 课程排班ID
 * @returns {Promise} 返回删除结果
 */
export function deleteCourse(id) {
    return request({
        url: `/course/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 保存或更新课程排班信息
 * @param {Object} course - 课程排班数据
 * @param {string} course.coachUsername - 教练用户名（逻辑外键）
 * @param {string} course.coachName - 教练姓名（冗余字段）
 * @param {string} course.campusId - 校区编号
 * @param {string} course.date - 课程日期（YYYY-MM-DD格式）
 * @param {string} course.startTime - 开始时间（HH:mm:ss格式）
 * @param {string} course.endTime - 结束时间（HH:mm:ss格式）
 * @param {number} course.duration - 时长（分钟）
 * @param {number} course.available - 是否可预约（1=是，0=否）
 * @param {string} course.img - 课程图片URL
 * @param {string} course.courtNumber - 球台编号
 * @returns {Promise} 返回保存结果
 */
export function saveCourse(course) {
    return request({
        url: '/course/save',
        method: 'post',
        data: course
    })
}

/**
 * 更新课程排班信息
 * @param {Object} course - 课程排班数据（包含ID）
 * @returns {Promise} 返回更新结果
 */
export function updateCourse(course) {
    return request({
        url: '/course/save',
        method: 'post',
        data: course
    })
}

/**
 * 批量删除课程排班
 * @param {Array} ids - 课程排班ID数组
 * @returns {Promise} 返回删除结果
 */
export function batchDeleteCourse(ids) {
    const deletePromises = ids.map(id => deleteCourse(id))
    return Promise.all(deletePromises)
}