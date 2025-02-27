from sqlalchemy import Column, ForeignKey, Integer, Float, String, DateTime, Text #https://docs.sqlalchemy.org/en/20/core/types.html
from sqlalchemy.orm import relationship # For the ORM not the DB
from database import Base  # Import Base from database.py
# class User(Base):
#     __tablename__ = "users"  # Table name in DB
#
#     id = Column(Integer, primary_key=True, index=True)
#     name = Column(String, nullable=False)
#     age = Column(Integer, nullable=True)
#     email = Column(String, unique=True, nullable=False)
class Categories(Base):
    __tablename__ = "categories"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    parent_id = Column(Integer, ForeignKey("categories.id", ondelete="CASCADE"), nullable=True)
    parent = relationship("Categories", back_populates="id", cascade="all, delete") 


# class Tasks(Base):
#     __tablename__ = "tasks"
#     id = Column(Integer, primary_key=True, index=True)
#     name = Column(String, nullable=False)
#     desc = Column(Text)
#     due = Column(DateTime)


    

