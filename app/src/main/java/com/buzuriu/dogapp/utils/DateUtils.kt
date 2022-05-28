package com.buzuriu.dogapp.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.models.MeetingObj
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
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

        private fun longToCalendar(time: Long?): Calendar? {
            var c: Calendar? = null
            if (time != null) {
                c = Calendar.getInstance()
                c.timeInMillis = time
            }
            return c
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isMeetingHappeningAtThisTime(meetingObj: MeetingObj, filterType: IFilterObj): Boolean {
            var start = Calendar.getInstance()
            var end = Calendar.getInstance()
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

            if (filterType.name == "Next Friday") {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999

                val localDate: LocalDate = LocalDate.now()

                val startLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.FRIDAY.toString())))
                val endLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.FRIDAY.toString())))

                val startLong = startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23,59,59).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

                start = longToCalendar(startLong)!!

                end = longToCalendar(endLong)!!

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "Next Saturday") {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999

                val localDate: LocalDate = LocalDate.now()

                val startLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SATURDAY.toString())))
                val endLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SATURDAY.toString())))

                val startLong = startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23,59,59).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

                start = longToCalendar(startLong)!!

                end = longToCalendar(endLong)!!

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            if (filterType.name == "Next Sunday") {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999


                val localDate: LocalDate = LocalDate.now()

                val startLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SUNDAY.toString())))
                val endLocalDate = localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SUNDAY.toString())))

                val startLong = startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23,59,59).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

                start = longToCalendar(startLong)!!
                end = longToCalendar(endLong)!!

                if (meetingDate.before(end.time) &&
                    meetingDate.after(start.time)
                )
                    return true
            }

            return false
        }
    }


}