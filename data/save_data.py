import traceback
import os
import pandas as pd
from load_data import get_mysql_engine
def save_table_data(engine, table):
    """
    Saves data from a MySQL table to a CSV file.

    Args:
        engine: A SQLAlchemy engine instance.
        table (str): The name of the table to save data from.

    Returns:
        None
    """
    
    sql = f'''SELECT * FROM {table}'''
    df = pd.read_sql_query(sql, engine)
    df.to_csv(f'./{table}_data.csv', index=False)

if __name__ == "__main__":
    engine = get_mysql_engine(f"mysql+pymysql://root:{os.getenv('MYSQL_PASSWORD')}@127.0.0.1:3306/comp47360", True)
    try:
        for table in ['zone', 'poitype', 'poi']:
            save_table_data(engine, table)
    except:
        print(traceback.format_exc())