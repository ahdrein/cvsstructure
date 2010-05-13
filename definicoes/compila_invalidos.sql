@".\define.sql"
conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS
-- Autor: Carlos Seikiti Matsuoka
-- Data: 01/09/2003
-- Alterado por Rodrigo Mezzacappa Barbosa
-- Data: 31/07/2007

-- Compila os objetos da base

declare
    v_Cursor integer;
    v_result integer;
begin
     -- cria um cursor sql
     v_Cursor := dbms_sql.Open_Cursor;
     --
     -- Compilando packages
     for c_InvalidObject in (
         select 'alter package ' || OBJECT_NAME || ' compile' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE like 'PACKAGE'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     --
     -- Compilando packages body
     for c_InvalidObject in (
         select 'alter package ' || OBJECT_NAME || ' compile BODY' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE like 'PACKAGE BODY'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     --
     -- Compilando views
     for c_InvalidObject in (
         select 'alter ' || OBJECT_TYPE || ' ' || OBJECT_NAME || ' compile' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE = 'VIEW'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     --
     -- Compilando triggers
     for c_InvalidObject in (
         select 'alter ' || OBJECT_TYPE || ' ' || OBJECT_NAME || ' compile' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE = 'TRIGGER'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     -- fecha o cursor
     dbms_sql.Close_Cursor(v_Cursor);
end;
/

declare
    v_Cursor integer;
    v_result integer;
begin
     -- cria um cursor sql
     v_Cursor := dbms_sql.Open_Cursor;
     --
     -- Compilando functions
     for c_InvalidObject in (
         select 'alter ' || OBJECT_TYPE || ' ' || OBJECT_NAME || ' compile' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE = 'FUNCTION'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     -- fecha o cursor
     dbms_sql.Close_Cursor(v_Cursor);
end;
/

declare
    v_Cursor integer;
    v_result integer;
begin
     -- cria um cursor sql
     v_Cursor := dbms_sql.Open_Cursor;
     --
     -- Compilando procedures
     for c_InvalidObject in (
         select 'alter ' || OBJECT_TYPE || ' ' || OBJECT_NAME || ' compile' as SQL_COMMAND,
                OBJECT_NAME
         from   USER_OBJECTS
         where  STATUS = 'INVALID'
         and    OBJECT_TYPE = 'PROCEDURE'
     ) loop
          begin
             -- compila a o comando sql
             dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
             -- executa o comando sql
             v_result := dbms_sql.execute(v_Cursor);
          exception
             when others then null;
          end;
     end loop;
     -- fecha o cursor
     dbms_sql.Close_Cursor(v_Cursor);
end;
/


declare
    v_Cursor   integer;
    v_result   integer;
    v_count    integer;
    v_Objetos  integer;
begin
     -- cria um cursor sql
     v_Cursor := dbms_sql.Open_Cursor;
     v_count := 999999999999999999999;
     --
     -- Compilando tudo
     loop
       v_Objetos := 0;
       for c_InvalidObject in (
           select 'alter ' || decode(O.OBJECT_TYPE,'PACKAGE BODY','PACKAGE',O.OBJECT_TYPE) || ' ' || O.OBJECT_NAME || ' compile ' || decode(O.OBJECT_TYPE,'PACKAGE BODY','BODY','') as SQL_COMMAND,
                  O.OBJECT_NAME
           from   USER_OBJECTS O
           where  O.STATUS = 'INVALID'
           and    not exists (select * from USER_ERRORS E where O.OBJECT_NAME = E.NAME)
       ) loop
            begin
               -- compila a o comando sql
               dbms_sql.parse(v_Cursor, c_InvalidObject.SQL_COMMAND, dbms_sql.native);
               -- executa o comando sql
               v_result := dbms_sql.execute(v_Cursor);
            exception
               when others then null;
            end;
            v_Objetos := v_Objetos + 1;
       end loop;
       if v_Objetos < v_count then
          v_count := v_Objetos;
       else
          exit;
       end if;
     end loop;
     -- fecha o cursor
     dbms_sql.Close_Cursor(v_Cursor);
end;
/
