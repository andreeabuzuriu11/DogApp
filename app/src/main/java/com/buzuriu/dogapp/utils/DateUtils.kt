package com.buzuriu.dogapp.utils

import android.annotation.SuppressLint
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.models.MeetingObj
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getDateString(timeInMillis: Long): String {
            val simpleDateFormat = SimpleDateFormat("EE MMM dd yyyy")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis;
            return simpleDateFormat.format(calendar.time);
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeString(timeInMillis: Long): String {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis;
            return simpleDateFormat.format(calendar.time);
        }

        fun isMeetingHappeningAtThisTime(meetingObj: MeetingObj, filterType: IFilterObj): Boolean {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            var meetingCalendar = Calendar.getInstance()

            start.timeInMillis = System.currentTimeMillis()
            end.time = Calendar.getInstance().time
            end[Calendar.HOUR_OF_DAY] = 23
            end[Calendar.MINUTE] = 59
            end[Calendar.SECOND] = 59
            end[Calendar.MILLISECOND] = 999

            meetingCalendar.timeInMillis = meetingObj.date!!

            val meetingDate = meetingCalendar.time

            if (filterType.name == "Today") {
                // all options for today are already selected above
                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "Tomorrow") {
                start.time = Calendar.getInstance().time
                start.add(Calendar.DATE, 1)
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.add(Calendar.DATE, 1)

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "This week") {
                start.time = Calendar.getInstance().time
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0

                var todayAsDayOfWeek = Calendar.DAY_OF_WEEK
                var daysTillSunday = 7 - todayAsDayOfWeek
                end.add(Calendar.DATE, daysTillSunday)

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "This month") {
                end.add(Calendar.MONTH, 1)
                end.set(Calendar.DAY_OF_MONTH, 1)
                end.add(Calendar.DATE, -1)

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "Next week") {
                start.time = Calendar.getInstance().time
                start.add(Calendar.WEEK_OF_MONTH, 1)
                start[Calendar.DAY_OF_WEEK] = 1
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.timeInMillis = System.currentTimeMillis()
                end.add(Calendar.WEEK_OF_MONTH, 1)
                end[Calendar.DAY_OF_WEEK] = 7

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "Next month") {
                start.time = Calendar.getInstance().time
                start.add(Calendar.MONTH, 1)
                start[Calendar.DAY_OF_MONTH] = 1
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.timeInMillis = System.currentTimeMillis()
                end.add(Calendar.MONTH, 2)
                end.set(Calendar.DAY_OF_MONTH, 1)
                end.add(Calendar.DATE, -1)

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }
            return false
        }
    }
}