#include <QApplication>
#include <QCalendarWidget>

int main(int argc, char **argv)
{
    QApplication app(argc, argv);

    QCalendarWidget calendar;
    calendar.setSelectedDate(calendar.selectedDate().addDays(3));
    calendar.setGridVisible(true);
    calendar.show();

    QCalendarWidget calendarMin;
    calendarMin.setMinimumDate(calendarMin.selectedDate().addDays(-7));
    calendarMin.setGridVisible(true);
    calendarMin.show();

    QCalendarWidget calendarMax;
    calendarMax.setMaximumDate(calendarMax.selectedDate().addDays(7));
    calendarMax.setGridVisible(true);
    calendarMax.show();

    return app.exec();
}
