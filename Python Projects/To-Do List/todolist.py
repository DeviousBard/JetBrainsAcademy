from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Date
from sqlalchemy.orm import sessionmaker
from datetime import datetime, timedelta, date

Base = declarative_base()


class Tasks(Base):
    __tablename__ = 'task'
    id = Column(Integer, primary_key=True)
    task = Column(String)
    deadline = Column(Date, default=datetime.today())

    def __repr__(self):
        return self.string_field


class DatabaseUtil:
    engine = create_engine('sqlite:///todo.db?check_same_thread=False')

    @staticmethod
    def create_table():
        Base.metadata.create_all(DatabaseUtil.engine)

    @staticmethod
    def get_session():
        session_maker = sessionmaker(bind=DatabaseUtil.engine)
        return session_maker()


def main():
    days_of_week = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    session = DatabaseUtil.get_session()
    while True:
        print("1) Today's tasks")
        print("2) Week's tasks")
        print("3) All tasks")
        print("4) Missed tasks")
        print("5) Add task")
        print("6) Delete task")
        print("0) Exit")
        menu_option = input()
        print()
        if menu_option == '0':
            break
        if menu_option == '1':
            tasks = session.query(Tasks).filter(Tasks.deadline == date.today()).all()
            # print(f"tasks: {tasks}")
            today = datetime.today()
            print(f"Today {today.strftime('%b')} {today.day}:")
            if len(tasks) == 0:
                print("Nothing to do!")
            index = 1
            for task_record in tasks:
                print(f"{index}. {task_record.task}")
                index += 1
            print()
        if menu_option == '2':
            today = datetime.today()
            curr_date = date.today()
            next_week = curr_date + timedelta(days=7)
            while curr_date < next_week:
                print(f"{days_of_week[curr_date.weekday()]} {curr_date.day} {curr_date.strftime('%b')}:")
                tasks = session.query(Tasks).filter(Tasks.deadline == curr_date).all()
                if len(tasks) == 0:
                    print("Nothing to do!")
                index = 1
                for task_record in tasks:
                    print(f"{index}. {task_record.task}")
                    index += 1
                print()
                curr_date = curr_date + timedelta(days=1)
        if menu_option == '3':
            tasks = session.query(Tasks).order_by(Tasks.deadline).all()
            print(f"All tasks:")
            if len(tasks) == 0:
                print("Nothing to do!")
            index = 1
            for task_record in tasks:
                task = task_record.task
                deadline = task_record.deadline
                print(f"{index}. {task}. {deadline.day} {deadline.strftime('%b')}")
                index += 1
            print()
        if menu_option == '4':
            today = date.today()
            tasks = session.query(Tasks).filter(Tasks.deadline < today).order_by(Tasks.deadline).all()
            print(f"Missed tasks:")
            if len(tasks) == 0:
                print("Nothing to do!")
            index = 1
            for task_record in tasks:
                task = task_record.task
                deadline = task_record.deadline
                print(f"{index}. {task}. {deadline.day} {deadline.strftime('%b')}")
                index += 1
            print()
        if menu_option == '5':
            print("Enter task")
            task = input()
            print("Enter deadline")
            deadline = input()
            new_task = Tasks(task=task, deadline=datetime.strptime(deadline, '%Y-%m-%d').date())
            session.add(new_task)
            session.commit()
            print("The task has been added!")
            print()
        if menu_option == '6':
            tasks = session.query(Tasks).order_by(Tasks.deadline).all()
            index = 1
            print("Choose the number of the task you want to delete:")
            for task_record in tasks:
                task = task_record.task
                deadline = task_record.deadline
                print(f"{index}. {task}. {deadline.day} {deadline.strftime('%b')}")
                index += 1
            task_index = int(input()) - 1
            task = tasks[task_index]
            session.delete(task)
            session.commit()
            print()


DatabaseUtil.create_table()
main()
