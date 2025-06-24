from sqlalchemy import Date, Column, Integer, String
from sqlalchemy.orm import declarative_base

from enum import Enum
from sqlalchemy import Enum as SqlEnum

Base = declarative_base()

class ScrapType(Enum):
    INTERN = "INTERN"
    COMPETITION = "COMPETITION"
    ACTIVITY = "ACTIVITY"
    EDUCATION = "EDUCATION"

class Scrap(Base):
    __tablename__= "scrap"

    id = Column(Integer, primary_key =True, index=True)
    scrap_type = Column(SqlEnum(ScrapType), nullable=False)
    title =Column(String(256), nullable=False)
    organizer_name=Column(String(100))
    d_day = Column(Integer)
    work_type = Column(String(256))
    region = Column(String(100))
    start_date = Column(Date)
    end_date = Column(Date)
    job_category = Column(String(256))
    homepage_url=Column(String(512))
    img_url=Column(String(256))

    benefits=Column(String(256))
    additional_benefits=Column(String(256))
    period=Column(String(256))
    recruit_number=Column(String(256))

    enterprise_type=Column(String(100))
    preferred_skills=Column(String(256))
    award=Column(String(256))
    cost=Column(String(256))
    employment=Column(String(256))
    on_off_line=Column(String(256))