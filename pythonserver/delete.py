import pymongo

"""
TODO:
INPUTS:
   row_id: a list of row_ids that need to be removed
   tb: table in the database that contains all stories
   valid: set to False if error occurs
OUTPUTS:
   NONE

entry keys in tb:
   'row_id', 'user_id', 'image_dir','quote','datetime','valid'
"""

# NOTE we only turn valid field in the corresponding entries
# into 0, instead of really removing the entries
def run_delete(row_id,tb,valid):
    pass
