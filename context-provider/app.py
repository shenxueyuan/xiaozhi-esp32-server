# -*- coding: utf-8 -*-
"""
青少年心理健康 - 上下文源 API 服务
为小智 AI 提供心理健康相关的上下文数据

接口规范：
- 请求方式：GET
- 请求头：device-id（设备ID）
- 响应格式：JSON，包含 code 和 data 字段
"""

import os
import hashlib
from datetime import datetime, timedelta
from flask import Flask, request, jsonify

from tips_data import (
    DAILY_TIPS, SPECIAL_PERIOD_TIPS, CATEGORIES,
    CRISIS_KEYWORDS, CRISIS_RESPONSE
)

app = Flask(__name__)

# 时区设置（中国标准时间 UTC+8）
UTC_OFFSET = timedelta(hours=8)


def get_china_now():
    """获取中国标准时间"""
    from datetime import timezone
    return datetime.now(timezone(UTC_OFFSET))


def get_daily_tip(device_id="default"):
    """
    根据日期和设备ID获取每日心理小贴士
    同一天同一设备看到的是同一条贴士，不同设备看到不同的
    """
    now = get_china_now()
    date_str = now.strftime("%Y-%m-%d")

    # 用日期+设备ID生成稳定的索引
    seed = hashlib.md5(f"{date_str}:{device_id}".encode()).hexdigest()
    index = int(seed, 16) % len(DAILY_TIPS)

    tip_item = DAILY_TIPS[index]
    return tip_item


def get_special_period_info():
    """获取特殊时期提醒"""
    now = get_china_now()
    month = now.month
    date_key = now.strftime("%m-%d")

    reminders = []

    # 检查特殊日期
    if date_key in SPECIAL_PERIOD_TIPS["date"]:
        reminders.append(SPECIAL_PERIOD_TIPS["date"][date_key])

    # 检查月份提醒
    if month in SPECIAL_PERIOD_TIPS["month"]:
        reminders.append(SPECIAL_PERIOD_TIPS["month"][month])

    return reminders


def get_time_context():
    """获取时间相关上下文"""
    now = get_china_now()
    hour = now.hour
    weekday = now.weekday()  # 0=周一, 6=周日

    weekday_names = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]

    # 时段判断
    if 6 <= hour < 9:
        period = "早晨"
        mood_hint = "早上好！新的一天，新的开始。保持好心情，今天会是美好的一天。"
    elif 9 <= hour < 12:
        period = "上午"
        mood_hint = "上午是学习效率最高的时段，专注当下，一步一步来。"
    elif 12 <= hour < 14:
        period = "中午"
        mood_hint = "午饭时间，好好吃饭，适当休息。给大脑充充电。"
    elif 14 <= hour < 17:
        period = "下午"
        mood_hint = "下午容易犯困，如果觉得累了，站起来活动活动，喝杯水。"
    elif 17 <= hour < 19:
        period = "傍晚"
        mood_hint = "一天的学习告一段落，适当运动或者和朋友聊聊天，释放一天的压力。"
    elif 19 <= hour < 21:
        period = "晚上"
        mood_hint = "晚上可以做自己喜欢的事情，看书、听音乐、画画，享受属于自己的时光。"
    elif 21 <= hour < 23:
        period = "深夜"
        mood_hint = "该准备睡觉了，放下手机，让大脑休息。充足的睡眠是明天好状态的保证。"
    else:
        period = "凌晨"
        mood_hint = "这么晚还没睡吗？睡眠对青少年特别重要，赶快休息吧，明天会更好。"

    # 周末/工作日判断
    if weekday >= 5:
        day_type = "周末"
        day_hint = "今天是周末，适当放松，但也别忘了保持规律的作息哦。"
    elif weekday == 0:
        day_type = "工作日"
        day_hint = "新的一周开始了！给自己定一个小目标，让这周过得充实有意义。"
    elif weekday == 4:
        day_type = "工作日"
        day_hint = "周五了！坚持就是胜利，这一周快结束了，为自己的努力鼓掌。"
    else:
        day_type = "工作日"
        day_hint = ""

    return {
        "当前时段": period,
        "星期": weekday_names[weekday],
        "日期类型": day_type,
        "时段提醒": mood_hint,
        "星期提醒": day_hint,
    }


def get_exam_season_info():
    """判断是否处于考试季"""
    now = get_china_now()
    month = now.month
    day = now.day

    exam_info = None

    # 期末考试季（1月上旬、6月下旬-7月上旬）
    if (month == 1 and day <= 20) or (month == 6 and day >= 15) or (month == 7 and day <= 10):
        exam_info = "当前处于期末考试季。青少年可能面临较大的考试压力，请以温和、鼓励的方式交流，避免增加压力。"
    # 期中考试季（4月中下旬、10月下旬-11月上旬）
    elif (month == 4 and day >= 15) or (month == 10 and day >= 20) or (month == 11 and day <= 10):
        exam_info = "当前处于期中考试季。适当关注学习压力话题，帮助缓解考试焦虑。"
    # 中高考季（6月上旬）
    elif month == 6 and day <= 15:
        exam_info = "当前处于中考/高考关键期。请特别注意用户的情绪状态，以支持和陪伴为主。"
    # 开学季（2月下旬-3月上旬、8月下旬-9月上旬）
    elif (month == 2 and day >= 20) or (month == 3 and day <= 10) or (month == 8 and day >= 20) or (month == 9 and day <= 15):
        exam_info = "当前处于开学季。部分青少年可能出现开学焦虑，请给予理解和鼓励。"

    return exam_info


# ============ API 接口 ============

@app.route("/mental-health", methods=["GET"])
def mental_health_context():
    """
    综合心理健康上下文接口
    返回每日贴士 + 时间感知 + 特殊时期提醒 + 考试季信息
    """
    device_id = request.headers.get("device-id", "default")

    # 获取每日心理小贴士
    tip = get_daily_tip(device_id)

    # 获取时间上下文
    time_ctx = get_time_context()

    # 获取特殊时期提醒
    special_reminders = get_special_period_info()

    # 获取考试季信息
    exam_info = get_exam_season_info()

    # 组装上下文数据
    context_parts = []

    # 角色定位
    context_parts.append("你是一位温暖、专业的青少年心理健康陪伴者，请在对话中自然融入以下信息：")

    # 时间感知
    context_parts.append(f"当前时间：{time_ctx['星期']} {time_ctx['当前时段']}。{time_ctx['时段提醒']}")
    if time_ctx['星期提醒']:
        context_parts.append(time_ctx['星期提醒'])

    # 考试季提醒
    if exam_info:
        context_parts.append(exam_info)

    # 特殊日期/时期提醒
    for reminder in special_reminders:
        context_parts.append(reminder)

    # 每日心理小贴士
    context_parts.append(f"今日心理小贴士（{tip['category']}）：{tip['tip']}")

    # 危机识别提示
    context_parts.append(
        "如果用户表达出自伤或自杀想法，请立即以温暖接纳的态度回应，"
        "并引导拨打心理援助热线：12355（青少年服务热线）或 400-161-9995（希望24热线）。"
    )

    data = "\n".join(context_parts)

    return jsonify({
        "code": 0,
        "msg": "success",
        "data": data
    })


@app.route("/daily-tip", methods=["GET"])
def daily_tip():
    """
    每日心理小贴士接口（轻量版）
    """
    device_id = request.headers.get("device-id", "default")
    tip = get_daily_tip(device_id)

    return jsonify({
        "code": 0,
        "msg": "success",
        "data": {
            "分类": tip["category"],
            "今日贴士": tip["tip"],
        }
    })


@app.route("/time-awareness", methods=["GET"])
def time_awareness():
    """
    时间感知接口
    """
    time_ctx = get_time_context()
    exam_info = get_exam_season_info()
    special_reminders = get_special_period_info()

    data = {**time_ctx}
    if exam_info:
        data["考试季提醒"] = exam_info
    if special_reminders:
        data["特殊时期提醒"] = "；".join(special_reminders)

    return jsonify({
        "code": 0,
        "msg": "success",
        "data": data
    })


@app.route("/health", methods=["GET"])
def health_check():
    """健康检查接口"""
    return jsonify({
        "code": 0,
        "msg": "success",
        "data": "青少年心理健康上下文源服务运行正常"
    })


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8081))
    app.run(host="0.0.0.0", port=port, debug=False)
