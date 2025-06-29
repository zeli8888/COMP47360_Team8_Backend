from sqlalchemy import text
from sqlalchemy import create_engine
import traceback
import os
import pandas as pd

def get_mysql_engine(database, no_echo=False):
    
    if no_echo != True and no_echo != False:
        raise Exception("Invalid echo option, choose either True or False!")
    
    engine = create_engine(database, echo = not no_echo)
    return engine

def commit_sql(engine, sql, val=None):
    """
    Commits a SQL query to the database using the provided engine.

    Args:
        engine: A SQLAlchemy engine instance.
        sql (str): The SQL query to execute.
        val (tuple, optional): Values to bind to the SQL query (default: None).

    Returns:
        result: The result of the SQL query execution.

    Raises:
        Exception: If the SQL query execution fails.
    """
    
    if val is None:
        with engine.connect() as connection:
            try:
                transaction = connection.begin()
                res = connection.execute(text(sql))
                transaction.commit()
                return res
            except Exception as e:
                traceback.print_exc()
                connection.rollback()
    else:
        with engine.connect() as connection:
            try:
                transaction = connection.begin()
                res = connection.execute(text(sql), val)
                transaction.commit()
                return res
            except Exception as e:
                traceback.print_exc()
                connection.rollback()
                
def load_table_data(engine, table):
    """
    Loads data from a CSV file into a MySQL table.

    Args:
        engine: A SQLAlchemy engine instance.
        table (str): The name of the table to load data into.

    Returns:
        None
    """
    df = pd.read_csv(f'./{table}_data.csv')
    df.to_sql(f'{table}', engine, if_exists='append', index=False, method='multi', chunksize=1000)
    
if __name__ == "__main__":
    engine = get_mysql_engine(f"mysql+pymysql://root:{os.getenv('MYSQL_PASSWORD')}@127.0.0.1:3306/comp47360", True)
    try:
        for table in ['zone', 'poitype', 'poi']:
            load_table_data(engine, table)
    except:
        print(traceback.format_exc())