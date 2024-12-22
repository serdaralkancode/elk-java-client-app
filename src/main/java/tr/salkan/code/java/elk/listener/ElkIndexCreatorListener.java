package tr.salkan.code.java.elk.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import tr.salkan.code.java.elk.annotation.GenericElkIndex;
import tr.salkan.code.java.elk.elkoperation.IElasticOperationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ElkIndexCreatorListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LogManager.getLogger(ElkIndexCreatorListener.class);
    private final IElasticOperationService elasticOperationService;
    private final ConfigurableApplicationContext applicationContext;

    public ElkIndexCreatorListener(IElasticOperationService elasticOperationService, ConfigurableApplicationContext applicationContext) {
        this.elasticOperationService = elasticOperationService;
        this.applicationContext = applicationContext;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOG.info("elk model scan listener starting");

        scanElkModelAnnotation();

        LOG.info("elk model scan listener ending");
    }

    private void scanElkModelAnnotation() {

        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(GenericElkIndex.class));

        Set<BeanDefinition> beanDefs = provider
                .findCandidateComponents("tr.salkan.code");
        List<String> annotatedBeans = new ArrayList<>();
        for (BeanDefinition bd : beanDefs) {
            if (bd instanceof AnnotatedBeanDefinition) {
                Map<String, Object> annotAttributeMap = ((AnnotatedBeanDefinition) bd)
                        .getMetadata()
                        .getAnnotationAttributes(GenericElkIndex.class.getCanonicalName());
                annotatedBeans.add(annotAttributeMap.get("indexName").toString());
            }
        }

        LOG.info("found total index count :" +  annotatedBeans.size());

        for(int i = 0; i < annotatedBeans.size(); i++)
        {
            if(!checkIfExistIndex(annotatedBeans.get(i)))
            {

                try {
                    createIndex(annotatedBeans.get(i));
                } catch (IOException e) {
                    LOG.error("hata",e);
                }
            }
        }

    }

    private boolean checkIfExistIndex(String indexname) {

        try {
            return elasticOperationService.checkIfExistIndex(indexname);
        } catch (IOException e) {
            LOG.error("hata",e);
        }

        return false;
    }

    private String createIndex(String indexname) throws IOException {

        String createdIndex =  elasticOperationService.createIndex(indexname);

        return createdIndex;

    }

}
