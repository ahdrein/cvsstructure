conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     TABELA: BG_MOEDA_CONVERSAO
--  ///////

delete from DEPEND_HEADER_ITEM_TMP;

delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = 'BG_MOEDA_CONVERSAO';

begin

  update	TAB_INTERFACE             
  set		DESCRICAO = '',
		CTL_NAME = 'BG_MOEDA_CONVERSAO', 
		PREFIX_FILE = 'MOECON', 
		GERAR_CTL = 'S', 
		PRIORIDADE = 0, 
		ODBC_SOURCE_NAME = '', 
		ODBC_USER = '', 
		ODBC_PASSWORD = '', 
		ODBC_TABLE_NAME = '', 
		ODBC_WHERE = '', 
		ODBC_SELECT_ESPECIFICO = '', 
		TIPO_INTERFACE = 'LOADER',
		ORACLE_INITIAL_EXTENT = '', 
		ORACLE_NEXT_EXTENT = '', 
		ORACLE_INDEX_TABLESPACE = '',
		ELIMINAR_REG_EXECUCAO = 'N',
		ID_SISTEMA = 'BG',
		PROCEDURE_NAME = 'PKG_SFW_MOEDA.PRC_MOEDA_CONVERSAO',
		SEPARADOR = '',
		CTL_FIXO = null,
		TRIGGER1 = null,
		TRIGGER2 = null,
		COMANDO_EXTRA_LOADER = ''
  where TABLE_NAME = 'BG_MOEDA_CONVERSAO';

  if SQL%notfound then
		insert into TAB_INTERFACE 
		(TABLE_NAME,
		DESCRICAO, 
		CTL_NAME, 
		PREFIX_FILE, 
		GERAR_CTL, 
		PRIORIDADE, 
		ODBC_SOURCE_NAME, 
		ODBC_USER, 
		ODBC_PASSWORD, 
		ODBC_TABLE_NAME, 
		ODBC_WHERE, 
		ODBC_SELECT_ESPECIFICO, 
		TIPO_INTERFACE, 
		ORACLE_INITIAL_EXTENT, 
		ORACLE_NEXT_EXTENT, 
		ORACLE_INDEX_TABLESPACE, 
		ELIMINAR_REG_EXECUCAO, 
		COMANDO_EXTRA_LOADER, 
		ID_SISTEMA, 
		PROCEDURE_NAME, 
		SEPARADOR)
		values
		('BG_MOEDA_CONVERSAO',
		'',
		'BG_MOEDA_CONVERSAO', 
		'MOECON', 
		'S', 
		0, 
		'', 
		'', 
		'', 
		'', 
		'', 
		'', 
		'LOADER', 
		'', 
		'', 
		'',
		'N',
		'',
		'BG',
		'PKG_SFW_MOEDA.PRC_MOEDA_CONVERSAO',
		'');
  end if;
end;
/


insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA_CONVERSAO', 'COD_MOEDA_ORIGEM', 'CHAR', '', '', '25', 
'1','Código da Moeda de origem','P_COD_MOEDA_ORIGEM','');

insert into DEPEND_HEADER_ITEM
(	TABLE_NAME,
	COLUMN_NAME,
	TABLE_NAME_HEADER,
	COLUMN_NAME_HEADER, 
	OBRIG)
values	
(	'BG_MOEDA_CONVERSAO' ,
	'COD_MOEDA_ORIGEM' ,
	'BG_MOEDA' ,
	'COD_MOEDA',
	'N' );

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA_CONVERSAO', 'COD_MOEDA_DESTINO', 'CHAR', '', '', '60', 
'2','Código da Moeda de destino','P_COD_MOEDA_DESTINO','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA_CONVERSAO', 'ID_CONVERSAO', 'CHAR', '', '', '3', 
'3','Código da conversão','P_ID_CONVERSAO','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA_CONVERSAO', 'ULTIMO_INFORMANTE', 'CHAR', '', '', '47', 
'4','Ultimo informante','P_ULTIMO_INFORMANTE','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA_CONVERSAO', 'ATUALIZACAO', 'CHAR', '', '', '14', 
'5','Data da atualização','P_ATUALIZACAO','');

commit;

set serveroutput on
exec PRC_SINCRONIZA_TABELA('BG_MOEDA_CONVERSAO');

CREATE OR REPLACE
TRIGGER TI_BG_MOEDA_CONVERSAO
BEFORE INSERT ON BG_MOEDA_CONVERSAO
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	select seq_id_interface.nextval into :new.id from dual;

  begin
    select id
    into :new.id_ref
    from BG_MOEDA
    where id_importacao = :new.id_importacao
    and COD_MOEDA = :new.COD_MOEDA_ORIGEM;
  exception
    when no_data_found then
      :new.id_ref := null;   
    when others then
      raise_application_error(-20001,'Falha em busca de header: ' || sqlerrm(sqlcode()));
  end;
 
end;
/


CREATE OR REPLACE
TRIGGER TIA_BG_MOEDA_CONVERSAO
AFTER INSERT ON BG_MOEDA_CONVERSAO
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)
	values (:new.id, :new.id_importacao, 'BG_MOEDA_CONVERSAO', :new.id_ref);
end;
/


CREATE OR REPLACE
TRIGGER TD_BG_MOEDA_CONVERSAO
BEFORE DELETE ON BG_MOEDA_CONVERSAO
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	delete from REGISTROS_INTERFACES where id = :old.id;
end;
/


 
-- //////
-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas
-- //////

