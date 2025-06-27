from sqlalchemy import Date, Column, Integer, String
from sqlalchemy.orm import declarative_base

from enum import Enum
from sqlalchemy import Text, Enum as SqlEnum

Base = declarative_base()

class ScrapType(Enum):
    INTERN = "INTERN"
    COMPETITION = "COMPETITION"
    ACTIVITY = "ACTIVITY"
    EDUCATION = "EDUCATION"

class Scrap(Base):
    __tablename__= "scrap"

    id = Column("scrap_id", Integer, primary_key =True, index=True)
    linkareer_id=Column(Integer, index=True)
    created_time = Column(Date)
    modified_time = Column(Date)
    scrap_type = Column(SqlEnum(ScrapType), nullable=False)
    title =Column(String(256), nullable=False)
    organizer_name=Column(String(100))
    start_date = Column(Date)
    end_date = Column(Date)
    job_category = Column(String(256))
    homepage_url=Column(String(512))
    img_url=Column(String(256))

    benefits=Column(String(256))
    eligibility=Column(String(256))

    on_off_line=Column(String(256))

    enterprise_type=Column(String(100))
    region = Column(String(100))